import scalatags.Text.all._

object Home {
  def home() =
    html(
      head(
        script("some script")
      ),
      body(backgroundColor:="#404040", color:="#dddddd")(
        h1("This is my title"),
        div(
          p("This is my first paragraph"),
          p("This is my second paragraph")
        ),
        a(href:="www.naver.com")(
          p("NAVER")
        ),
        p(hidden)(
          "I'm hidden"
        )
      )
    )
}
