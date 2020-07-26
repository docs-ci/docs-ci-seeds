import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

def pipe_foler = "Pipelines"

  folder("$pipe_folder"){}

  pipelineJob("rebuild_matrix") {
    keepDependencies()
    concurrentBuild(false)

    def repo = dsl.SEED_JOB.scm.userRemoteConfigs.get(0).getUrl()
    def ref  = dsl.SEED_JOB.scm.getBranches().get(0).getName()

    definition {
      cpsScm {
        scm {
          git {
            remote { url(repo) }
            branch(ref)
            extensions {
              pathRestriction {
                includedRegions("pipelines/rebuild-matrix.groovy")
                excludedRegions(null)
              }
            }
          }
        }
        "pipelines/rebuild-matrix.groovy"
      }
    }
  }
