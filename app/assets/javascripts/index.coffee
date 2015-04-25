$ ->
  $.get "/templates/get", (templates) ->
    $.each templates, (index, template) ->
      $('#templates').append $("<li>").text template.name