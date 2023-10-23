title hdwa-project-control
chcp 65001
for %%j in (hdwa-project-control*.jar) do java -jar -Dfile.encoding=utf-8 -Dspring.profiles.active=项目id  %%j