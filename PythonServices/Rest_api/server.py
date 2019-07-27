# local modules
import config

connex_app = config.connex_app

connex_app.add_api("swagger.yml")

@connex_app.route("/")
def home():

    return ("Reality Check")


if __name__ == "__main__":
    connex_app.run(debug=True)