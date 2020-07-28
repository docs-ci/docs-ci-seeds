node { label "master" }

parallel {
  def arch_list = ["cos7", "cos8"]
  arch_list.each { arch ->
    stage('build' + arch) {
        build(
          job: "SciPipe/rebuild_" + arch,
           parameters: [],
        )
    }
  }
}
