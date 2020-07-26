pipeline {
  agent {
    node {
      label "master"
    }
  }

  def pipe_foler = "Pipelines"

  def arch_list = ["cos7", "cos8"]

  def matrix = [:]

  stages {
    stage('build') {
      arch_list.each { arch ->
        matrix[arch] = build(
          job: "SciPipe/rebuild_" + arch,
          parameters: [],
          wait: true,
        )
      }
    }
  }
}
