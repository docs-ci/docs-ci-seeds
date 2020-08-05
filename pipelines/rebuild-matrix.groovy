//node { label "master" }

def arch_list = ["centos7", "centos8"]
def rebuild_jobs_folder = "Rebuilds"
def matrix = [:]

def buildParams = [
  string(name: 'REFS', value: REFS),
  string(name: 'PRODUCTS', value: PRODUCTS),
  string(name: 'SPLENV_REF', value: SPLENV_REF),
]

println buildParams
println REFS
println PRODUCTS
println SPLENV_REF

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
    stage('get bID ' + arch) {
      node("master") {
        def manifest_file = "/var/jenkins_home/${arch}/lsstsw/build/manifest.txt"
        def manifest  = readFile(manifest_file)
        def buildid = manifest =~ /(?m)^BUILD=(b.*)m ? m[0][1] : null
        println "Build on ${arch} completed. Build is is ${buildid}."
      }
    }
  }
}

parallel matrix

