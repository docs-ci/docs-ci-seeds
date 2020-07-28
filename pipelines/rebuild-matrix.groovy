node { label "master" }

def arch_list = ["centos7", "centos8"]
def matrix = [:]

arch_list.each { arch ->
  matrix[arch] = {
    stage('build' + arch) {
      build(
        job: "SciPipe/rebuild_" + arch,
        parameters: [],
        wait: true,
      )
    }
  }
}

parallel matrix
