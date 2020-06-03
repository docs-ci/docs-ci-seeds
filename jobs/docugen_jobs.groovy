// Docugeneration jobs
//import org.yaml.snakeyaml.Yaml
//def docs_list = new Yaml().load(readFileFromWorkSpace('etc/docugen.yaml'))

script = '''WORK_DIR=`pwd`
source "${WORK_DIR}/.docugen"

git diff
git add *.tex 
git add jira_imgs/
if [ -d "attachments" ]; then
  git add attachments
fi
git status
git diff-index --quiet HEAD || git commit --author "Docsteady <noreply@lsst.org>" -m "Jenkins automatic update from Jira"
'''

///   folder
folder('docs'){ description('All Docugen Jobs.') }
folder('docs/DM'){ description('DM Docugen Jobs.') }
folder('docs/SitCom'){ description('SitCom Docugen Jobs.') }


///  loop creation
// docs_list.collect { -> doc
//    println "${doc}"
//}

/// DM documents autogener
job('docs/DM/LDM-540-docugen') {
    label('docugen')
    def gitBranch = 'jira-sync'
    def gitUrl = 'https://github.com/lsst/LDM-540'
    scm { git(gitUrl,'master',{node -> node / 'extensions' << '' } ) }
    triggers { scm('H 4 * * 1-5') }
    steps {
        // https://stackoverflow.com/questions/11511390/jenkins-git-plugin-detached-head
        shell('git checkout -B ' + gitBranch)
        shell('git pull origin ' + gitBranch)
        shell(script)
    }
    publishers {
        git {
            pushOnlyIfSuccess()
            branch('origin', gitBranch)
        }
    }
}


job('docs/DM/LDM-552-docugen') {
    label('docugen')
    def gitBranch = "jira-sync"
    def gitUrl = 'https://github.com/lsst/LDM-552'
    scm { git(gitUrl) }
    triggers { scm('H 4 * * 1-5') }
    steps {
        shell('git checkout -B ' + gitBranch)
        shell('git pull origin ' + gitBranch)
        shell(script)
    }
    publishers {
        git {
            pushOnlyIfSuccess()
            branch('origin', gitBranch)
        }
    }
}


job('docs/DM/LDM-639-docugen') {
    label('docugen')
    def gitBranch = "jira-sync"
    def gitUrl = 'https://github.com/lsst/LDM-639'
    scm { git(gitUrl,'master',{node -> node / 'extensions' << '' } ) }
    triggers { scm('H 4 * * 1-5') }
    steps { 
        shell('git checkout -B ' + gitBranch)
        shell('git pull origin ' + gitBranch)
        shell(script)
    }
    publishers {
        git {
            pushOnlyIfSuccess()
            branch('origin', gitBranch)
        }
    }
}


job('docs/DM/DMTR-182-docugen') {
    label('docugen')
    def gitBranch = "tickets/DM-17123"
    def gitUrl = 'https://github.com/lsst-dm/DMTR-182'
    scm { git(gitUrl) }
    triggers { scm('H 4 * * 1-5') }
    steps {
        shell('git checkout -B ' + gitBranch)
        shell('git pull origin ' + gitBranch)
        shell(script)
    }
    publishers {
        git {
            pushOnlyIfSuccess()
            branch('origin', gitBranch)
        }
    }
}
