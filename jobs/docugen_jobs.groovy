// Docugeneration jobs
@Grab(group='org.yaml', module='snakeyaml', version='1.5')
import org.yaml.snakeyaml.Yaml
// https://stackoverflow.com/questions/47443106/jenkins-dsl-parse-yaml-for-complex-processing
def workDir = SEED_JOB.getWorkspace()
def docs_list = new Yaml().load(("${workDir}/etc/docugen.yaml" as File).text)

def git_credentials = "c81223b1-4e27-466f-9e8d-83ea7eea6b2f"

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

/// Creating docs  folder
folder('docs'){ description('All Docugen Jobs.') }
//folder('docs/DM'){ description('DM Docugen Jobs.') }
//folder('docs/SitCom'){ description('SitCom Docugen Jobs.') }


///  loop creation
def docs_folders = []
docs_list.each { doc, values ->
    println "Defining job for ${doc}"
    if (!(values.folder in docs_folders)) {
        docs_folders.add("${values.folder}")
        folder("docs/${values.folder}"){}
    }
    def gitUrl = values.gitUrl
    def gitBranch = values.gitBranch
    def checkout_script = 'git checkout -B ' + gitBranch + '\n'
    checkout_script = checkout_script + 'git pull origin ' + gitBranch
    job("docs/${values.folder}/${doc}-docugen") {
        label('docugen')
        scm {
            git {
                remote {
                    url(gitUrl)
                    credentials(git_credentials)
                }
                extensions { }
            }
        }
        triggers { cron('H 4 * * *') }
        steps {
            // https://stackoverflow.com/questions/11511390/jenkins-git-plugin-detached-head
            shell(checkout_script)
            shell(script)
        }
        publishers {
            git {
                pushOnlyIfSuccess()
                branch('origin', gitBranch)
            }
        }
    }
}
