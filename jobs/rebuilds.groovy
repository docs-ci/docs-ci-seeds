
def arch_list = ["centos7", "centos8"]

def jobs_dir = "BuildJobs"

folder(jobs_dir){ description('Rebuild Jobs.') }


arch_list.each { arch ->
  job("${jobs_dir}/rebuild-${arch}") {
    label(arch)

    parameters {
      stringParam('REFS', null, 'Whitespace delimited list of git references to pass to rebuild with -t option')
      stringParam('PRODUCTS', null, 'Whitespace delimited list of EUPS products to build.')
      stringParam('SPLENV_REF', null, 'Conda env ref. If not specified it will use the default from lsstsw.')
    }

    build_script = '''#!/bin/bash
set +x

#--------  update current lsstsw branch
$HOME/lsstsw/bin/update

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
echo "   source $HOME/lsstsw/bin/envconfig $envref"
source $HOME/lsstsw/bin/envconfig $envref

#--------------------------------
echo "Executing rebuild:" 
echo "       rebuild $buildrefs $products"
rebuild $buildrefs $products
grep BUILD $HOME/lsstsw/build/manifest.txt | awk -F '=' '{print $2}' > $HOME/lsstsw/build/build.id
'''

    steps {
        shell(build_script)
    }
  }
}

