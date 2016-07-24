import json
import redis
from flask import Flask, request, redirect


app = Flask(__name__)


r = redis.StrictRedis(host='redis', port=6379, db=0)


@app.route("/", methods=['POST'])
def login():
    token = request.form['token']
    demand = json.loads(r.get(token))
    demand['principal'] = 'test_mctestface'
    r.set(token, json.dumps(demand))
    # There's definitely a better way to build a URL safely :-)
    return redirect("https://shib.local:4443/idp/Authn/Shim/Return?token=" + token, code=302)


@app.route("/<token>", methods=['GET'])
def auth(token):
    data = r.get(token)
    return """
    <p>Welcome to auth:<p>
    <form action="/" method="post">
    <input type="submit" name="login" value="login"/>
    <input type="hidden" name="token" value="{}"/>
    </form>
    """.format(token)


if __name__ == "__main__":
    app.run(host='0.0.0.0')
