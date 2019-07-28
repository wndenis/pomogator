/**
 * Moves the map 
 *
 * @param  {H.Map} map      A HERE Map instance within the application
 * @param {H.geo.Point} coordinate  The location of the marker
 * @param {String} html             Data associated with the marker
 */

var coordinates = {
  'lat':"string",
  'lng':"string"
}

function addMarkerToGroup(group,behavior,lat, lng, content) {
  var marker = new H.map.Marker({lat:lat, lng:lng}, {
    // mark the object as volatile for the smooth dragging
    volatility: true
  });
  //debugger;
  // Ensure that the marker can receive drag events
  marker.draggable = true;
  // add custom data to the marker
  
  marker.setData(content);
  
  group.addObject(marker);
  
  // disable the default draggability of the underlying map
  // when starting to drag a marker object:
  map.addEventListener('dragstart', function(ev) {
    var target = ev.target;
    if (target instanceof H.map.Marker) {
      behavior.disable();
    }
  }, false);
  // re-enable the default draggability of the underlying map
  // when dragging has completed
  map.addEventListener('dragend', function(ev) {
    var target = ev.target;
    if (target instanceof H.map.Marker) {
      var coordLat = target.b.lat;
      var coordLng = target.b.lng;
      
      marker.setGeometry({lat:coordLat,lng:coordLng})
      coordinates = {lat:coordLat,lng:coordLng};

  // Get an instance of the geocoding service:
  var geocoder = platform.getGeocodingService();
      geocoder.reverseGeocode(
        {
          prox: `${coordLat},${coordLng},150`,
          mode: 'retrieveAddresses',
          maxresults: 1
        },
        function(result){
          var location = result.Response.View[0].Result[0];

          // Create an InfoBubble at the returned location with
          // the address as its contents:
          ui.addBubble(new H.ui.InfoBubble({
            lat: location.Location.DisplayPosition.Latitude,
            lng: location.Location.DisplayPosition.Longitude
          }, { content: location.Location.Address.Label + `<br><button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal">OK</button>` }));
        },
        function(e) { alert(e); });
      
      behavior.enable();


    }
  }, false);

  // Listen to the drag event and move the position of the marker
  // as necessary
   map.addEventListener('drag', function(ev) {
    var target = ev.target,
        pointer = ev.currentPointer;
    if (target instanceof H.map.Marker) {
      target.setGeometry(map.screenToGeo(pointer.viewportX, pointer.viewportY));
    }
  }, false);
}



function addMarker(map,lat,lng) {
  group = new H.map.Group();

  map.addObject(group);



  // Create the parameters for the reverse geocoding request:
  var reverseGeocodingParameters = {
    prox: `${lat},${lng},150`,
    mode: 'retrieveAddresses',
    maxresults: 1
  };

  // Define a callback function to process the response:
  function onSuccessShowInfo(result) {
      var location = result.Response.View[0].Result[0];

      // Create an InfoBubble at the returned location with
      // the address as its contents:
      ui.addBubble(new H.ui.InfoBubble({
        lat: location.Location.DisplayPosition.Latitude,
        lng: location.Location.DisplayPosition.Longitude
      }, { content: location.Location.Address.Label + `<br><button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal">OK</button>` }));

      //addMarkerToGroup(group,behavior,lat, lng, location.Location.Address.Label);

  };
  // add 'tap' event listener, that opens info bubble, to the group
  group.addEventListener('tap', function (evt) {
    // Call the geocode method with the geocoding parameters,
    // the callback and an error callback function (called if a
    // communication error occurs):
      geocoder.reverseGeocode(
        {
          prox: `${evt.target.b.lat},${evt.target.b.lng},150`,
          mode: 'retrieveAddresses',
          maxresults: 1
        },
        onSuccessShowInfo,
        function(e) { alert(e); });
        coordinates = evt.target.getGeometry();
    });

  function onSuccessGetMarkerContent(result) {
    var location = result.Response.View[0].Result[0];
    addMarkerToGroup(group,behavior,lat, lng, location.Location.Address.Label);
};

  // Get an instance of the geocoding service:
  var geocoder = this.platform.getGeocodingService();

  geocoder.reverseGeocode(
    reverseGeocodingParameters,
    onSuccessGetMarkerContent,
    function(e) { alert(e); });

}



  function setUpClickListener(map) {
    // Attach an event listener to map display
    // obtain the coordinates and display in an alert box.

    var getCoord = function (evt){
      var coord = map.screenToGeo(evt.currentPointer.viewportX,
        evt.currentPointer.viewportY);

      addMarker(map,coord.lat, coord.lng);
      map.removeEventListener('tap',getCoord);
    }

    map.addEventListener('tap', getCoord);

  }
  
  /**
   * Boilerplate map initialization code starts below:
   */
  
  //Step 1: initialize communication with the platform
  // In your own code, replace variable window.apikey with your own apikey
  var platform = new H.service.Platform({
    apikey: window.apiKey

  });
  var defaultLayers = platform.createDefaultLayers();
  
  //Step 2: initialize a map - this map is centered over Europe
  var map = new H.Map(document.getElementById('map'),
    defaultLayers.vector.normal.map,{
    center: {lat:50, lng:5},
    zoom: 4,
    pixelRatio: window.devicePixelRatio || 1
  });
  // add a resize listener to make sure that the map occupies the whole container
  window.addEventListener('resize', () => map.getViewPort().resize());
  
  //Step 3: make the map interactive
  // MapEvents enables the event system
  // Behavior implements default interactions for pan/zoom (also on mobile touch environments)
  var behavior = new H.mapevents.Behavior(new H.mapevents.MapEvents(map));
  
  // Create the default UI components
  var ui = H.ui.UI.createDefault(map, defaultLayers);
  
  // Now use the map as required...
  window.onload = function () {
    setUpClickListener(map); 
  }
  function geocode(value) {
    var geocoder = platform.getGeocodingService(),
      geocodingParameters = {
        searchText: value,
        jsonattributes : 1
      };
  
    geocoder.geocode(
      geocodingParameters,
      onSuccess,
      onError
    );
  }
  /**
   * This function will be called once the Geocoder REST API provides a response
   * @param  {Object} result          A JSONP object representing the  location(s) found.
   *
   * see: http://developer.here.com/rest-apis/documentation/geocoder/topics/resource-type-response-geocode.html
   */
  function onSuccess(result) {
    var locations = result.response.view[0].result;
    addMarker(map,locations[0].location.displayPosition.latitude,locations[0].location.displayPosition.longitude);

  }

function onError(error) {
  alert('Can\'t reach the remote server');
}    

function sendMarkerDataToBack(){
  console.log(coordinates);
  var link = "http://10.30.21.152:5000/api"
  var xhr = new XMLHttpRequest();

  var json = JSON.stringify({
  "latitude": coordinates.lat,
  "longitude": coordinates.lng
  });

  xhr.open("POST", `${link}/order`, true)
  xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');

  xhr.onreadystatechange = function () {
    if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
        console.log(xhr.responseText);
    };
};

  // Отсылаем объект в формате JSON и с Content-Type application/json
  // Сервер должен уметь такой Content-Type принимать и раскодировать
  xhr.send(json);
}
              