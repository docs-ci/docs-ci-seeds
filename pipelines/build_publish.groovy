
def arch_list = ["centos7", "centos8"]
def arch_src = "centos8"
def jobs_folder = "BuildJobs"
def matrix = [:]

// build srcs
//   -  this is a single job, to be executed in the newest linux architecture
def buildSrcParams = [
  string(name: 'REFS', value: REFS),
  string(name: 'PRODUCTS', value: PRODUCTS),
  string(name: 'SPLENV_REF', value: SPLENV_REF),
  string(name: 'DISTRIBTAG', value: DISTRIBTAG),
]

stage('Build Src Packages') {
  build(
    job: "${jobs_folder}/src-distrib",
    parameters: buildSrcParams,
    wait: true,
  )
}
stage('Get DistribTag') {
  // this is required only if no DISTRIBTAG is provided
  node("master") {
    def distribtag_file = "/var/jenkins_home/${arch_src}/lsstsw/build/distrib.tag"
    def String distribtag = readFile(distribtag_file).trim()
    println "Source distribution tagged as ${distribtag} (job input value: $DISTRIBTAG)."
  }
}

// tag the src build buildID (options for development)

// build tarballs
//   -  this is a a job matrix to be executed in all supported architectures
def buildTblsParams = [
  string(name: 'PRODUCTS', value: PRODUCTS),
  string(name: 'DISTRIBTAG', value: distribtag),
]

arch_list.each { arch ->
  matrix[arch] = {
    stage('Tarballs for ' + arch) {
      println arch + "  -------------"
      build(
        job: "${jobs_folder}/tbls-distrib-${arch}",
        parameters: buildTblsParams,
        wait: true,
      )
    }
  }
}

parallel matrix


// trigger donstream jobs
//   -  preparatino of the docker image for distribution
//   -  gatering ap/drp metrics
//   -  documentation


