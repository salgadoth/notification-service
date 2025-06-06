module.exports = {
    apps: [
        {
            name: "notification-service",
            script: "/home/admin/.sdkman/candidates/java/22.0.2-oracle/bin/java",
            args: "-jar email-service/target/email-service-1.0-SNAPSHOT.jar",
            watch: false,
            env: {
                NODE_ENV: "production",
            }
        },
    ]
}