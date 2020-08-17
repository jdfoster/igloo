# Gradle Plugin to Calculate MD5 Checksums with Kotlin

This gradle plugin demonstrates executing of parallel work using the Worker API. We
utilise this to calculate MD5 checksums for files within the source directory. This
follows the [Using the Worker API] guide, while ports the Java code to Kotlin; 
within our plugin we access the WorkerExecution service using [Service Injection].

This plugin makes use of gradle incubating APIs, such as the _Worker API_,
therefore this library should be considered __unstable__.

[Using the Worker API]: https://guides.gradle.org/using-the-worker-api/
[Service Injection]: https://docs.gradle.org/current/userguide/custom_gradle_types.html#service_injection
