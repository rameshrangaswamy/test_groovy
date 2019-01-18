pipeline {
    agent { node { label 'testgroup' } }
    tools {
        maven 'maven'
        scannerHome = tool 'demoscanner'
    }
    options {
      skipDefaultCheckout true
    }
    stages {
        stage('Deploy') {
            steps {
                // clone project and install dependencies
                git url: 'https://github.com/rameshrangaswamy/demoCICDjob.git', branch: 'master'
            }
        }
                stage('Analyse') {
            steps {
                // coverage tests initialization script
                sh '''mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent \
                  install -Dmaven.test.failure.ignore=true || true'''
            }
        }
                stage('Report') {
            steps {
                 writeFile file: "${pwd()}/sonar-project.properties", text: """
                 #Mandatory meta data required
                 sonar.projectKey=sonarcheck
                 sonar.projectName=SonarDemo
                 sonar.projectVersion=1.0
                 #path to the src directory of the maven project
                 sonar.sources=src
                 sonar.jacoco.reportPath=target\\coverage-reports\\jacoco-unit.exec
                 #sonar.sources=src/main/java/
                 sonar.language=java
                 sonar.java.binaries=target/
                 """
             }
         }
            stage('sonaranalysis') {
            withSonarQubeEnv('My SonarQube Server') {
                 sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -U -Dmaven.test.failure.ignore=true org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar'
         }
            }
    }
}
