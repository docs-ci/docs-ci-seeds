node { label "master" }

def arch_list = ["centos7", "centos8"]
def rebuild_jobs_folder = "Rebuilds"
def matrix = [:]

arch_list.each { arch ->
  matrix[arch] = {
    stage('build' + arch) {
      build(
        job: "${rebuild_jobs_folder}/rebuild_${arch}",
        parameters: [],
        wait: true,
      )
    }
  }
}

parallel matrix
