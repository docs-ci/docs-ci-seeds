import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

folder('Pipelines'){ description('All Pipelines Jobs.') }

pipelineJob("Pipelines/publish_matrix") {
  description('Identify a suitable build (rebuild), publishu the sources and publish the tarballs.')

  properties {
    pipelineTriggers {
      triggers {
        cron {
          spec('H 4 * * *')
        }
      }
    }
  }

  def repo = SEED_JOB.scm.userRemoteConfigs.get(0).getUrl()
  def ref  = SEED_JOB.scm.getBranches().get(0).getName()

  parameters {
    stringParam('REFS', null, 'Whitespace delimited list of git references to pass to rebuild with -t option.')
    stringParam('PRODUCTS', "lsst_distrib", 'Whitespace delimited list of EUPS products to build.')
    stringParam('SPLENV_REF', null, 'Conda env ref. If not specified it will use the default from lsstsw.')
    stringParam('DISTRIBTAG', null, 'Distribution tag (eups). If not specified the buildID will be used.')
  }

  //concurrentBuild(true)

  definition {
    cpsScm {
      scm {
        git { remote { url(repo) } }
      }
      scriptPath("pipelines/build_publish.groovy")
    }
  }
}
