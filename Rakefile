
desc "Build the Shim jar files"
task :build do
 sh "mvn clean && mvn package"
end

namespace :demo do 


  desc "Run demo"
  task :start => ["demo:provision"] do

    cd "demo" do
      sh "docker-compose  up -d --build --force-recreate"
    end

  end

  desc "Stop demo"
  task :stop do

    cd "demo" do
      sh "docker-compose  down"
    end

  end

  desc "View live Docker logs for demo"
  task :tail do
    cd "demo" do
      sh "docker-compose logs -f"
    end
  end

  desc "Install war file into demo"
  task :provision => ["rake:build"] do
    mkdir_p "demo/idp/optfs/shibboleth-idp/edit-webapp/WEB-INF/lib"
    sh "cp target/shabti-shim-jar-with-dependencies.jar  demo/idp/optfs/shibboleth-idp/edit-webapp/WEB-INF/lib/shabti-shim.jar"
   # sh "cp target/shabti-shim.war demo/idp/optfs/shim/war/"
  end

  desc "Regenerate certificates"
  task :regenerate_certs do

    names = [
      "idp.localhost.demo.university",
      "sp.localhost.federated-example.org",
      "auth.localhost.demo.university"
    ]
    dir = "demo/frontend/certs"
    
    names.each do |name|
      sh "openssl req -new -x509 -days 365 -nodes  -out #{dir}/#{name}-cert.pem -keyout #{dir}/#{name}-key.pem -subj '/C=GB/L=Manchester/O=Digital Identity/CN=#{name}'"
    end

  end

end