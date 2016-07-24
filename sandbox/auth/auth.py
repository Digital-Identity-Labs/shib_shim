import redis
from flask import Flask, request


app = Flask(__name__)


r = redis.StrictRedis(host='redis', port=6379, db=0)


@app.route("/", methods=['POST'])
def login():
    print """LOGIN: {token}""".format(token=request.query['token'])


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
    app.run()
