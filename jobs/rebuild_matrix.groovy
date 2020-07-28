import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

folder('Pipelines'){ description('All Pipelines Jobs.') }

pipelineJob("Pipelines/rebuild_matrix") {
  properties{
      pipelineTriggers{
        triggers {
          cron{"H 4 0 0 0"}
        }
      }
  }

  def repo = SEED_JOB.scm.userRemoteConfigs.get(0).getUrl()
  def ref  = SEED_JOB.scm.getBranches().get(0).getName()

  definition {
    cpsScm {
      scm {
        git { remote { url(repo) } }
      }
      scriptPath("pipelines/rebuild-matrix.groovy")
    }
  }
}
