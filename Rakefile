
namespace :demo do 

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