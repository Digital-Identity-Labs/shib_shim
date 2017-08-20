from __future__ import print_function
import json
import redis
import sys
from flask import Flask, request, redirect



app = Flask(__name__)


r = redis.StrictRedis(host='redis', port=6379, db=0)


@app.route("/", methods=['POST'])
def login():
    token = request.form['token']
    principal = request.form['principal']
    demand = json.loads(r.get(token))
    demand['principal'] = principal
    demand['x-ignore']  = "not relevant"
    r.set(token, json.dumps(demand))
    # There's definitely a better way to build a URL safely :-)
    #return redirect(demand['return_url'], code=302)
    app.logger.info('%s is logging in', demand['principal'])
    sys.stdout.flush()
    #return redirect("https://idp.localhost.demo.university/idp/Authn/shim/return/" + token, code=302)
    return redirect(demand['return_url'], code=302)


@app.route("/<token>", methods=['GET'])
def auth(token):
    data = r.get(token)
    demand = json.loads(data)
    app.logger.info('%s is the return_url', demand['return_url'])
    sys.stdout.flush()
    return """
    <p>Welcome to auth:<p>
    <form action="/" method="post">
    Please choose a principal name:
    <input type="text" name="principal" value="testy_mctestface"/><br>
    <input type="submit" name="login" value="login"/>
    <input type="hidden" name="token" value="{}"/>
    </form>
    """.format(token)

@app.route("/favicon.ico", methods=['GET'])
def icon():
    return "", 404

if __name__ == "__main__":
    app.run(host='0.0.0.0')
