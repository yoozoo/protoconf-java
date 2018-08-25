node {
	ws("${JENKINS_HOME}/jobs/${JOB_NAME}/builds/${BUILD_ID}/") {
		dir('protoconf-java') {
			stage('Checkout'){
				checkout scm
			}
			stage('build') {
				echo 'Builing'
			}
			stage('Test'){
				echo 'Testing'
			}
			stage('Github Publish'){
				withCredentials([string(credentialsId: 'github-token', variable: 'TOKEN')]) {
					sh """curl -i -H 'Authorization: token ${TOKEN}' \
                             -X POST https://api.github.com/repos/yoozoo/protoconf-java/releases -d \
                             '{ \
                            "tag_name": "v1.0.0", \
                            "target_commitish": "master", \
                            "name": "v1.0.0", \
                            "body": "Description of the release", \
                            "draft": false, \
                            "prerelease": false \
                          }'"""
				}
			}
		}
	}
}
