
def arch_list = ["centos7", "centos8"]

def jobs_dir = "Rebuilds"

folder(jobs_dir){ description('Rebuild Jobs.') }

arch_list.each { arch ->
  job("${jobs_dir}/rebuild-${arch}") {
    label(arch)
    script = '#!/bin/bash\n' +
             'set +x\n\n' +
             'source $JENKINS_HOME/' + arch + '/lsstsw/bin/envconfig\n\n' +
             'rebuild lsst_distrib'
    steps {
      shell(script)
    }
  }
}
