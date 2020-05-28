
def gitUrl = 'https://github.com/lsst/LDM-639'

script = '''#!/bin/bash
git checkout jira-sync

docsteady generate-spec "/Data Management/Acceptance|LDM-639" jira_docugen.tex

git diff
git add jira_docugen.tex 
git add jira_docugen.appendix.tex 
git add jira_imgs/
git diff-index --quiet HEAD || git commit --author "Docsteady <noreply@lsst.org>" -m "Jenkins automatic update commit"
'''

job('docs/DM/LDM-639-docugen') {
    label('master')
    scm { git(gitUrl) }
    triggers { scm('H 4 * * 1-5') }
    steps { shell(script) }
}
