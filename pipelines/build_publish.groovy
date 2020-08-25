
def arch_list = ["centos7", "centos8"]
def jobs_folder = "BuildJobs"
def matrix = [:]

def buildParams = [
  string(name: 'REFS', value: REFS),
  string(name: 'PRODUCTS', value: PRODUCTS),
  string(name: 'SPLENV_REF', value: SPLENV_REF),
  string(name: 'DISTRIBTAG', value: DISTRIBTAG),
]

// build srcs
//   -  this is a single job, to be executed in the newest linux architecture

stage('Build Src Packages') {
  build(
    job: "${jobs_folder}/src-distrib",
    parameters: buildParams,
    wait: true,
  )
}
stage('Get DistribTag') {
  // this is required only if no DISTRIBTAG is provided
  node("master") {
    def distribtag_file = "/var/jenkins_home/${arch}/lsstsw/build/distrib.tag"
    def distribtag  = readFile(distribtag_file)
    println "Source distribution tagged as ${distribtag} (input value: $DISTRIBTAG."
  }
}

// tag the src build buildID (options for development)

// build tarballs
//   -  this is a a job matrix to be executed in all supported architectures

arch_list.each { arch ->
  matrix[arch] = {
    stage('Tarballs for ' + arch) {
      println arch + "  -------------"
      build(
        job: "${jobs_folder}/tbls-distrib-${arch}",
        parameters: buildParams,
        wait: true,
      )
    }
    stage('get bID ' + arch) {
      node("master") {
        def buildid_file = "/var/jenkins_home/${arch}/lsstsw/build/build.id"
        def buildid  = readFile(buildid_file)
        println "Build on ${arch} completed. BuildID is ${buildid}."
      }
    }
  }
}

parallel matrix


// trigger donstream jobs
//   -  preparatino of the docker image for distribution
//   -  gatering ap/drp metrics
//   -  documentation


