var AUTOCOMPLETION_URL = 'https://autocomplete.geocoder.api.here.com/6.2/suggest.json',
    ajaxRequest = new XMLHttpRequest(),
    query = '';

/**
 * If the text in the text box  has changed, and is not empty,
 * send a geocoding auto-completion request to the server.
 *@param  {H.Map} map  
 * @param {Object} textBox the textBox DOM object linked to this event
 * @param {Object} event the DOM event which fired this listener
 */
function autoCompleteListener(textBox, event) {

  if (query != textBox.value){
    if (textBox.value.length >= 1){

      /**
      * A full list of available request parameters can be found in the Geocoder Autocompletion
      * API documentation.
      *
      */
      var params = '?' +
        'query=' +  encodeURIComponent(textBox.value) +   // The search text which is the basis of the query
        '&beginHighlight=' + encodeURIComponent('<mark>') + //  Mark the beginning of the match in a token. 
        '&endHighlight=' + encodeURIComponent('</mark>') + //  Mark the end of the match in a token. 
        '&gen=9'+
        '&maxresults=10' +  // The upper limit the for number of suggestions to be included 
                          // in the response.  Default is set to 5.
        '&app_id=' + window.restAppId +
        '&app_code=' + window.restAppCode;
      ajaxRequest.open('GET', AUTOCOMPLETION_URL + params );
      ajaxRequest.send();
    }
  }
  query = textBox.value;
}


/**
 *  This is the event listener which processes the XMLHttpRequest response returned from the server.
 */
function onAutoCompleteSuccess() {
 /*
  * The styling of the suggestions response on the map is entirely under the developer's control.
  * A representitive styling can be found the full JS + HTML code of this example
  * in the functions below:
  */
  clearOldSuggestions();
  addSuggestionsToPanel(this.response);  // In this context, 'this' means the XMLHttpRequest itself.
}


/**
 * This function will be called if a communication error occurs during the XMLHttpRequest
 */
function onAutoCompleteFailed() {
  alert('Ooops!');
}

// Attach the event listeners to the XMLHttpRequest object
ajaxRequest.addEventListener("load", onAutoCompleteSuccess);
ajaxRequest.addEventListener("error", onAutoCompleteFailed);
ajaxRequest.responseType = "json";



/**
 * Boilerplate map initialization code starts below:
 */





var bubble;


/**
 * Function to Open/Close an infobubble on the map.
 * @param  {H.geo.Point} position     The location on the map.
 * @param  {String} text              The contents of the infobubble.
 */
function openBubble(position, text){
 if(!bubble){
    bubble =  new H.ui.InfoBubble(
      position,
      // The FO property holds the province name.
      {content: '<small>' + text+ '</small>'});
    ui.addBubble(bubble);
  } else {
    bubble.setPosition(position);
    bubble.setContent('<small>' + text+ '</small>');
    bubble.open();
  }
}


//param {Object} response

/**
 * Removes all H.map.Marker points from the map and adds closes the info bubble
 */
function clearOldSuggestions(){
     if(bubble){
       bubble.close();
     }
 }
 
 /**
  * Format the geocoding autocompletion repsonse object's data for display
  *
  * @param {Object} response
  */
 var filter = new Array();
 function addSuggestionsToPanel(response){
    filter = [];
    document.getElementById('select').innerHTML='';
    
    response['suggestions'].forEach(element => {
        filter.push(element.label);
        var string = document.createElement('option');
        string.innerHTML = element.label;
        document.getElementById('select').appendChild(string);
    });
 }
