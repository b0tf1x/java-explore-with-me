# java-explore-with-me
https://github.com/b0tf1x/java-explore-with-me/pull/5

# проект "Explore With Me"
<h3>Приложение представляет из себя афишу. </h3>
<p><b>В этой афише можно предложить какое-либо событие от выставки до похода в кино и собрать компанию для участия в нём.</b></p>

<img src="../../../Downloads/Image (1).png">

<h2>Два сервиса</h2>
<p><b>Основной сервис содержит всё необходимое для работы продукта;</b></p>
<p><b>Сервис статистики хранит количество просмотров и позволяет делать различные выборки для анализа работы приложения.</b></p>

<h2>Эндпоинты:</h2>
<ul>
<li>/compilations - для работы с подборками событий</li>
<li>/admin/compilations - для работы с подборками событий от лица администратора</li>
<p></p>
<li>/categories - для работы с категориями</li>
<li>/admin/categories - для работы с категориям от лица администратора</li>
<p></p>
<li>/events - для работы с событиями</li>
<li>/admin/events - для работы </li>
<li>/users/{userId}/events - приватный эндпоинт для работы с событиями</li>
<p></p>
<li>/users/{userId}/requests - приватный эндпоинт для работы с запросами на участие</li>
<p></p>
<li>/admin/users - для работы с пользователями от лица администратора</li>
<p></p>
<li>/users/{userId}/comments - для работы с комментариями</li>
<li>/admin/users/comment - для работы с комментариями от лица администратора</li>
</ul>