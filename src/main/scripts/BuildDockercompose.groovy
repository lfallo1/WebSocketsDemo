String template = new File("${project.basedir}/src/main/docker/DockerComposeTemplate".toString()).getText()

def postgresUser = project.properties['postgres.user']
def postgresPassword = project.properties['postgres.password']
println "postgresUser " + postgresUser
println "postgresPassword " + postgresPassword

def imageTag = project.properties['git.commit.id.abbrev']

def dockerFileText = new groovy.text.SimpleTemplateEngine().createTemplate(template)
        .make([gitCommit: imageTag, postgresUser: postgresUser, postgresPassword: postgresPassword])

println "writing file " + "${project.basedir}/src/main/docker/compose/docker-compose.yml"
File dockerFile = new File("${project.basedir}/src/main/docker/compose/docker-compose.yml".toString())

dockerFile.withWriter('UTF-8') { writer ->
    writer.write(dockerFileText)
}