title hdwa-project-sdk
chcp 65001
java -jar -Xms20g -Xmx20g -XX:+UseG1GC -XX:+UseLargePages -XX:ConcGCThreads=8 -XX:ParallelGCThreads=10 -Dfile.encoding=utf-8 -DprojectId=Pj2102110210 hdwa-project-sdk-0.0.1.jar