// Docugeneration jobs


script = '''#!/bin/bash
#git checkout jira-sync
git pull

WORK_DIR=`pwd`
source "${WORK_DIR}/.docugen"

git diff
git add jira_docugen.tex 
git add jira_docugen.appendix.tex 
git add jira_imgs/
git diff-index --quiet HEAD || git commit --author "Docsteady <noreply@lsst.org>" -m "Jenkins automatic update commit"
'''


folder('docs'){ description('All Docugen Jobs.') }
folder('docs/DM'){ description('DM Docugen Jobs.') }
folder('docs/SitCom'){ description('SitCom Docugen Jobs.') }


job('docs/DM/LDM-639-docugen') {
    label('docugen')
    def branch = "jira-sync"
    def gitUrl = 'https://github.com/lsst/LDM-639'
    scm { git(gitUrl) }
    triggers { scm('H 4 * * 1-5') }
    steps { 
        shell('git checkout ' + branch)
        shell(script)
    }
}
