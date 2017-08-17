import json
import redis
from flask import Flask, request, redirect


app = Flask(__name__)


r = redis.StrictRedis(host='redis', port=6379, db=0)


@app.route("/", methods=['POST'])
def login():
    token = request.form['token']
    principal = request.form['principal']
    demand = json.loads(r.get(token))
    demand['principal'] = principal
    r.set(token, json.dumps(demand))
    # There's definitely a better way to build a URL safely :-)
    return redirect(demand['return_to'], code=302)


@app.route("/<token>", methods=['GET'])
def auth(token):
    data = r.get(token)
    return """
    <p>Welcome to auth:<p>
    <form action="/" method="post">
    Please choose a principal name:
    <input type="text" name="principal" value="testy_mctestface"/><br>
    <input type="submit" name="login" value="login"/>
    <input type="hidden" name="token" value="{}"/>
    </form>
    """.format(token)


if __name__ == "__main__":
    app.run(host='0.0.0.0')
