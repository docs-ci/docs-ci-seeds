
def arch_list = ["centos7", "centos8"]

def jobs_dir = "Rebuilds"

folder(jobs_dir){ description('Rebuild Jobs.') }


arch_list.each { arch ->
  job("${jobs_dir}/rebuild-${arch}") {
    label(arch)

    parameters {
      stringParam('REFS', null, 'Whitespace delimited list of git references to pass to rebuild with -t option')
      stringParam('PRODUCTS', null, 'Whitespace delimited list of EUPS products to build.')
      stringParam('SPLENV_REF', null, 'Conda env ref. If not specified it will use the default from lsstsw.')
    }

    script = '#!/bin/bash\n' +
             'set +x\n\n' +
             'REFS=$1\n' +
             'PRODUCTS=$2\n' +
             'SPLENV_REF=$3\n\n' +
             'envref=""\n' +
             'if [[ "${SPLENV_REF}" ]]; then\n' +
             '  envref="-r ${SPLENV_REF}"\n' +
             'fi\n' +
             'source $JENKINS_HOME/' + arch + '/lsstsw/bin/envconfig $envref\n\n' +
             'buildrefs=""\n' +
             'if [[ "${REFS}" ]]; then\n' +
             '  for r in ${REFS[*]}; do\n' +
             '    buildrefs+=("-r $r")\n' +
             '  done\n' +
             'fi\n' +
             'rebuild $buildrefs lsst_distrib'
    steps {
      shell(script)
    }
  }
}
