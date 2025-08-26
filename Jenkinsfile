pipeline {
  agent any

  options {
    buildDiscarder(logRotator(numToKeepStr: '20'))
    timestamps()
  }

  // Fallback trigger if webhook not set yet (checks every 5 min)
  triggers { pollSCM('H/5 * * * *') }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build') {
      steps {
        // If wrapper isn't in repo yet, the OR part will generate it
        sh './gradlew clean build -x test || ./gradlew wrapper && ./gradlew clean build -x test'
      }
    }

    stage('Test + Coverage Gate') {
      steps {
        sh './gradlew test jacocoTestReport check'
      }
      post {
        always {
          junit 'build/test-results/test/*.xml'
          archiveArtifacts artifacts: 'build/reports/jacoco/test/html/**', allowEmptyArchive: true
        }
      }
    }

    stage('Archive Artifact') {
      steps {
        archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
      }
    }
  }

  post {
    success { echo "✅ Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}" }
    unstable { echo "⚠️ Unstable (likely coverage below threshold)" }
    failure {
      emailext(
        subject: "❌ Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        body: """Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
URL: ${env.BUILD_URL}""",
        to: 'team@example.com'
      )
      // slackSend channel: '#builds', message: "❌ ${env.JOB_NAME} #${env.BUILD_NUMBER} failed: ${env.BUILD_URL}"
    }
  }
}
