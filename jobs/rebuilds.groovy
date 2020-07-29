
def arch_list = ["centos7", "centos8"]

def jobs_dir = "Rebuilds"

folder(jobs_dir){ description('Rebuild Jobs.') }

arch_list.each { arch ->
  job("${jobs_dir}/rebuild-${arch}") {
    label(arch)

    parameters {
      stringParam('REFS', null, 'Whitespace delimited list of git references to build.  Priority is highest -> lowest from left to right.  The "master" branch, or thebranch from repos.yaml, is implicitly appended to the right side of the list; do not specify "master" explicitly.')
      stringParam('PRODUCTS', null,
      'Whitespace delimited list of EUPS products to build.')
      stringParam('SPLENV_REF', null, 'Conda env ref. If not specified it will use the default from lsstsw.')
    }

    script = '#!/bin/bash\n' +
             'set +x\n\n' +
             'source $JENKINS_HOME/' + arch + '/lsstsw/bin/envconfig\n\n' +
             'rebuild lsst_distrib'
    steps {
      shell(script)
    }
  }
}
