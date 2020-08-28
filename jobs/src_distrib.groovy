
def arch = "centos8"

def jobs_dir = "BuildJobs"

folder(jobs_dir){ description('Build Jobs.') }

job("${jobs_dir}/src-distrib") {
  label(arch)

  parameters {
    stringParam('REFS', null, 'Whitespace delimited list of git references to pass to rebuild with -t option')
    stringParam('PRODUCTS', null, 'Whitespace delimited list of EUPS products to build.')
    stringParam('SPLENV_REF', null, 'Conda env ref. If not specified it will use the default from lsstsw.')
    stringParam('DISTRIBTAG', "", 'Optional EUPS distribution tag. If none is provided, the timestamp will be used.')
  }

  update_script = '''#!/bin/bash
set +x
echo "[j>] update (Jenkins worker's lsstsw)"
$HOME/lsstsw/bin/update
'''

  build_script = '''#!/bin/bash
set +x
#--------  defining parameters
envref=""
if [[ "$SPLENV_REF" ]]; then
  envref="-r $SPLENV_REF"
fi
buildrefs=""

for r in ${REFS[*]}; do
  buildrefs+="-r $r"
done
products=""
if [[ "$PRODUCTS" ]]; then
  products=$PRODUCTS
else
  products="lsst_distrib"
fi
#--------------------------------
echo "Sourcing environment:"
echo "[j>] source $HOME/lsstsw/bin/envconfig $envref"
source $HOME/lsstsw/bin/envconfig $envref
#--------------------------------
echo "Executing rebuild:" 
echo "[j>] rebuild $buildrefs $products"
rebuild $buildrefs $products
grep BUILD $HOME/lsstsw/build/manifest.txt | awk -F '=' '{print $2}' > $HOME/lsstsw/build/build.id
'''

  publishsrc_script = '''#!/bin/bash
set +x
# this could be made more complex (a little bit), to handle eups_tags is provided

products=""
if [[ "$PRODUCTS" ]]; then
  products=$PRODUCTS
else
  products="lsst_distrib"
fi

# src-publish will enable the required env, depending on the build
# therefore, lsstsw/bin is not in $PATH (envconfig has not been colled yet)
# the script needs to be called using the absolute path
echo "[j>] $HOME/lsstsw/bin/src-publish -n -t $DISTRIBTAG $products"
$HOME/lsstsw/bin/src-publish -n -e 1 -v -t "$DISTRIBTAG" "$products"
if [[ "$DISTRBTAG" ]]; then
  echo "$DISTRIBTAG" > $HOME/lsstsw/build/distrib.tag
else
  grep BUILD $HOME/lsstsw/build/manifest.txt | awk -F '=' '{print $2}' > $HOME/lsstsw/build/distrib.tag
fi
'''

  steps {
    shell(update_script)
    shell(build_script)
    shell(publishsrc_script)
  }
}
