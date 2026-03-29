package org.farhan.mbt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.farhan.mbt.config.JmsConfig;
import org.farhan.mbt.core.Converter;
import org.farhan.dsl.grammar.IResourceRepository;
import org.farhan.mbt.exception.PublishingException;
import org.farhan.mbt.exception.TransformationException;
import org.farhan.mbt.model.TransformableFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import jakarta.jms.JMSException;
import jakarta.jms.Queue;

@Service
public class AsciiDoctorService {

    private static final Logger logger = LoggerFactory.getLogger(AsciiDoctorService.class);
    private final JmsTemplate jmsTemplate;

    @Autowired
    public AsciiDoctorService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public TransformableFile convertObject(Converter mojo, String fileName, String contents) {
        try {
            String input = contents == null ? "" : contents;
            // The replace \r is because when run on a windows machine the tests return a \r
            // but not on a linux machine
            String result = mojo.convertFile(fileName, input).replace("\r", "");
            String normalizedInput = input.replace("\r", "");
            if (result.equals(normalizedInput)) {
                return new TransformableFile(fileName, "");
            }
            // Debug: find first difference
            int minLen = Math.min(result.length(), normalizedInput.length());
            for (int i = 0; i < minLen; i++) {
                if (result.charAt(i) != normalizedInput.charAt(i)) {
                    int start = Math.max(0, i - 20);
                    int endR = Math.min(result.length(), i + 20);
                    int endI = Math.min(normalizedInput.length(), i + 20);
                    logger.info("Diff at pos {} in {}: result=[{}] input=[{}]", i, fileName,
                            result.substring(start, endR).replace("\n", "\\n"),
                            normalizedInput.substring(start, endI).replace("\n", "\\n"));
                    break;
                }
            }
            if (result.length() != normalizedInput.length()) {
                logger.info("Length diff in {}: result={} input={}", fileName, result.length(), normalizedInput.length());
            }
            return new TransformableFile(fileName, result);
        } catch (Exception e) {
            throw new TransformationException(fileName, e);
        }
    }

    public void clearObjects(Converter mojo) {
        try {
            mojo.clearProjects();
        } catch (Exception e) {
            throw new TransformationException("Clearing objects", e);
        }
    }

    public ArrayList<TransformableFile> getObjectNames(Converter mojo, String tags) {
        ArrayList<TransformableFile> fileList = new ArrayList<TransformableFile>();
        try {
            // TODO this is temp, there should be a separate class like the IResourceRepository
            // if not the object repo itself. For a given tag, it should keep track of the
            // source files and output files checksums
            for (String fileName : (mojo).getFileNames()) {
                fileList.add(new TransformableFile(fileName, ""));
                logger.debug("fileName: " + fileName);
            }
            return fileList;
        } catch (Exception e) {
            throw new TransformationException("Getting object names for tags: " + tags, e);
        }
    }

    public List<TransformableFile> getFileChecksums(IResourceRepository repository, String tags) {
        List<TransformableFile> fileList = new ArrayList<>();
        try {
            for (String checksumFileName : repository.list(tags, "checksums/", "")) {
                String checksum = repository.get(tags, checksumFileName);
                String originalFileName = checksumFileName.replaceFirst("^checksums/", "");
                TransformableFile tf = new TransformableFile(originalFileName, null, tags);
                tf.setFileChecksum(checksum);
                fileList.add(tf);
            }
        } catch (Exception e) {
            throw new TransformationException("Getting file checksums for tags: " + tags, e);
        }
        return fileList;
    }

    public void deleteObject(IResourceRepository repository, String tags, String fileName) {
        try {
            repository.delete(tags, "checksums/" + fileName);
        } catch (Exception e) {
            throw new TransformationException("Deleting object: " + fileName, e);
        }
    }

    public void convertSourceObject(Converter mojo, TransformableFile mtr) {
        try {
            jmsTemplate.convertAndSend(JmsConfig.SOURCE_FILES_QUEUE, mtr);
            MessageConsumer.IN_FLIGHT = 1;
            // TODO temp hack until separate queues are created (see issue #195)
            long waitStart = System.currentTimeMillis();
            while (MessageConsumer.IN_FLIGHT == 1) {
                if (System.currentTimeMillis() - waitStart > 60000) {
                    logger.warn("Timed out waiting for message processing: {}", mtr.getFileName());
                    MessageConsumer.IN_FLIGHT = 0;
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Thread interrupted while waiting for message processing to complete", e);
                }
            }
        } catch (Exception e) {
            throw new PublishingException(mtr.getFileName(), e);
        }
        if (MessageConsumer.IN_FLIGHT == -1) {
            // This is only temp, remove it when IN_FLIGHT is removed
            throw new TransformationException(mtr.getFileName());
        }
    }

    /**
     * Wait for the queue to be empty
     * 
     * @param timeoutSeconds Maximum time to wait in seconds
     * @return true if queue is empty, false if timeout occurred
     */
    public boolean waitForQueueToClear(int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMillis = TimeUnit.SECONDS.toMillis(timeoutSeconds);

        try (jakarta.jms.Connection connection = jmsTemplate.getConnectionFactory().createConnection();
             jakarta.jms.Session session = connection.createSession(false, jakarta.jms.Session.AUTO_ACKNOWLEDGE)) {
            Queue queue = session.createQueue(JmsConfig.SOURCE_FILES_QUEUE);

            while (System.currentTimeMillis() - startTime < timeoutMillis) {
                // Check queue depth
                int queueSize = getQueueSize(queue);
                if (queueSize == 0) {
                    return true;
                }

                // Wait a bit before checking again
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Queue clearing interrupted", e);
                    return false;
                }
            }

            return false; // Timeout occurred
        } catch (JMSException e) {
            throw new PublishingException("Waiting for queue to clear", e);
        }
    }

    /**
     * Get the current size of the queue
     * 
     * @param queue The queue to check
     * @return The number of messages in the queue
     * @throws JMSException If there's an error accessing the queue
     */
    private int getQueueSize(Queue queue) throws JMSException {
        return jmsTemplate.browse(queue, (session, browser) -> {
            int count = 0;
            while (browser.getEnumeration().hasMoreElements()) {
                browser.getEnumeration().nextElement();
                count++;
            }
            return count;
        });
    }

}
