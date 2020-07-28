
def arch_list = ["centos7", "centos8"]

def jobs_dir = "Rebuilds"


folder(job_dir){ description('Rebuild Jobs.') }

arch_list.each { arch ->
  job("${jobs_dir}/${values.folder}/rebuild-${arch}") {
    label(arch)
    script = '#!/bin/bash' +
             'set +x\n' +
             'source $JENKINS_HOME/' + arch + '/lsstsw/bin/envconfig\n' +
             'rebuild lsst_distrib'
    steps {
      shell(script)
    }
  }
}
