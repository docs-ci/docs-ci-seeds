// Job to generate and plush tarballs, for different platforms
//
def arch_list = ["centos7", "centos8"]

def jobs_dir = "BuildJobs"

folder(jobs_dir){ description('Build Jobs.') }

arch_list.each { arch ->
  job("${jobs_dir}/tarballs_${arch}") {
    label(arch)

    parameters {
      stringParam('PRODUCTS', null, 'Whitespace delimited list of EUPS products to build.')
      stringParam('DISTRIBTAG', null, 'EUPS tag created with a build and publish of src packages.')
    }

    update_script = '''#!/bin/bash
set +x
#--------  update current lsstsw branch
echo "[j>] update (node's lsstsw)"
$HOME/lsstsw/bin/update
'''

    publishtarballs_script = '''#!/bin/bash
set +x

if [[ ! "$PRODUCTS" ]]; then
  echo "[j>] Parameter PRODUCTS is mandantory."
  exit -1
fi
if [[ ! "$DISTRIBTAG" ]]; then
  echo "[j>] Parameter DISTRIBTAG is mandantory."
  exit -1
fi

# tarball-publish will enable the required env, depending on the build
# therefore, lsstsw/bin is not in $PATH (envconfig has not been colled yet)
# the script needs to be called using the absolute path
echo "[>] $HOME/lsstsw/bin/tarball-publish -t $DISTRIBTAG $PRODUCTS"
$HOME/lsstsw/bin/tarballs-publish -t $DISTRIBTAG $PRODUCTS
'''

    steps {
      shell(update_script)
      shell(publishtarballs_script)
    }
  }
}
