package org.farhan.sheepdog.eclipse;

import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.lsp4e.LanguageServers;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.text.IDocument;

public class GenerateCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor == null) {
			return null;
		}
		ITextEditor textEditor = editor.getAdapter(ITextEditor.class);
		if (textEditor == null) {
			return null;
		}
		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		if (document == null) {
			return null;
		}
		IEditorInput input = editor.getEditorInput();
		if (!(input instanceof IFileEditorInput)) {
			return null;
		}
		String uri = ((IFileEditorInput) input).getFile().getLocationURI().toString();
		ExecuteCommandParams params = new ExecuteCommandParams(
				"asciidoc.generate2",
				List.of(new com.google.gson.JsonPrimitive(uri)));

		LanguageServers.forDocument(document)
				.computeAll(server -> server.getWorkspaceService().executeCommand(params));
		return null;
	}
}
