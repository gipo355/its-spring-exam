live reload

<https://stackoverflow.com/questions/52092504/spring-boot-bootrun-with-continuous-build>

<https://stackoverflow.com/questions/26773292/how-to-update-gradle-dependencies-from-command-line>

<https://docs.spring.io/spring-boot/docs/1.5.16.RELEASE/reference/html/using-boot-devtools.html>

to refresh dependencies:

gradle build --refresh-dependencies

for continuous build 2 terminals:

<!-- gradle build --continuous -->

gradle -t :bootJar

gradle bootRun
