
def call() {
    pipeline {
        agent any
        stages {
            stage('Hello World') {
                steps {
                    script {
                        sh 'echo "Hello World"'
                    }
                }
            }
        }
    }
}
