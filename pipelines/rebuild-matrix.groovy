//node { label "master" }

def arch_list = ["centos7", "centos8"]
def rebuild_jobs_folder = "BuildJobs"
def matrix = [:]

def buildParams = [
  string(name: 'REFS', value: REFS),
  string(name: 'PRODUCTS', value: PRODUCTS),
  string(name: 'SPLENV_REF', value: SPLENV_REF),
]

println buildParams

arch_list.each { arch ->
  matrix[arch] = {
    stage('build ' + arch) {
      println arch + "  -------------"
      build(
        job: "${rebuild_jobs_folder}/rebuild-${arch}",
        parameters: buildParams,
        wait: true,
      )
    }
    //stage('get bID ' + arch) {
    //  node("master") {
    //    def buildid_file = "/var/jenkins_home/${arch}/lsstsw/build/build.id"
    //    def buildid  = readFile(buildid_file)
    //    println "Build on ${arch} completed. BuildID is ${buildid}."
    //  }
    //}
  }
}

parallel matrix

// vim jobs/publish_matrix.groovy
stage('Publish Packages') {
  build(
    job: "Pipelines/publish_matrix",
    parameters: buildParams,
    wait: false,
  )
}

