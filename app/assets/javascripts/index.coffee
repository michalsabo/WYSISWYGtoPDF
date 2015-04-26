$ ->
  $.get "/templates/get", (templates) ->
    $.each templates, (index, template) ->
      $('#my_templates').append $("<li>").html( '<b>Name: </b>' + template.name +  ' <a = href="/templates/edit/' +  template.id + '">EDIT</a>')

$ ->
  $.get "/templates/getadded", (templates) ->
    $.each templates, (index, template) ->
      $('#added_templates').append $("<li>").html( '<b>Name: </b>' + template.name + ", <b>owner:</b>  " + template.owner +  ' <a = href="/templates/edit/' +  template.id + '">EDIT</a>')


$ ->
  $.get "/groups/get", (groups) ->
    $.each groups, (index, group) ->
      $('#my_groups').append $("<li>").html('<b>Name: </b>' + group.name +  ' <a = href="/groups/edit/' +  group.id + '">EDIT</a>')

$ ->
  $.get "/groups/getadded", (groups) ->
    $.each groups, (index, group) ->
      $('#added_groups').append $("<li>").html('<b>Name: </b>' + group.name + ", <b>owner:</b> " + group.id +  ' <a = href="/groups/edit/' +  group.id + '">EDIT</a>')