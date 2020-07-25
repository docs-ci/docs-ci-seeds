
def pipe_foler = "Pipelines"

folder("$pipe_folder"){}


pipeline("$pipe_folder/rebuild_matrix") {

  def arch_list = ["cos7", "cos8"]

  def matrix = [:]

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
