cd ..
call git reset --hard HEAD
call git clean -fdx
call git pull
call gradle release
call git push
cd scripts
