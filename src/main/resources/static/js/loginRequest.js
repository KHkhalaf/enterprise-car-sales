$( document ).ready(function() {

  // SUBMIT FORM
    $("#loginForm").submit(function(event) {
    // Prevent the form from submitting via the browser.
    event.preventDefault();
    ajaxPost();
  });


    function ajaxPost(){

      // PREPARE FORM DATA
      var formData = {
        email : $("#email").val(),
        password :  $("#password").val()
      }

      // DO POST
      $.ajax({
      type : "POST",
      contentType : "application/json",
      url : "http://localhost:8081/auth/login",
      data : JSON.stringify(formData),
      dataType : 'json',
      success : function(result) {
      window.location.href="http://localhost:8081/car/listForSale";
        console.log(result);
      },
      error : function(e) {
        console.log("ERROR: ", e);
      window.location.href="http://localhost:8081/auth/accountNotVerified";
      }
    });
    }
})