pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh "rm -rf build/libs"
            }
        }

        stage('Build') {
            steps {
                sh "JAVA_HOME=${env.JDK_17} ./gradlew build -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"
            }
        }

        stage('Archive') {
            steps {
                archive includes: 'build/libs/*.jar'
                junit allowEmptyResults: true, testResults: 'build/test-results/**/*.xml'
            }
        }

        stage('Deploy') {
            steps {
                sh "JAVA_HOME=${env.JDK_17} ./gradlew publishMavenJavaPublicationToMavenRepository -PBUILD_NUMBER=${env.BUILD_NUMBER} -PDEPLOY_DIR=${env.MAVEN_DEPLOY_DIR} --no-daemon"
            }
        }
    }
}