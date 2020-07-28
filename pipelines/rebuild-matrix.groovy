node { label "master" }

    stage('build') {
      def arch_list = ["cos7", "cos8"]
      def matrix = [:]

      arch_list.each { arch ->
        matrix[arch] = build(
          job: "SciPipe/rebuild_" + arch,
          parameters: [],
          wait: false,
        )
      }

    }
