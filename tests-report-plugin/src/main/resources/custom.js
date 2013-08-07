/**
 * @author Innokenty Shuvalov
 *         innokenty@yandex-team.ru, vk.com/innocent, ishuvalov@pipeinpipe.info
 */
$(function () {

	const SPEED = 'fast';

	// при клике на тайтл кейса, его содержимое сворачивается/разворачивается
	$('.case > .title').click(function (e) {
		var title = $(this);
		var caseElem = title.closest('.case')
			.toggleClass('collapsed')
			.toggleClass('expanded');

		$('.screenshot, .urls', caseElem).slideToggle(SPEED);
	});

	function getBorderWidth(el, type) {
		return parseInt(el.css('border-' + type + '-width').replace('px', ''));
	}

	function getImageHeightWithBorder(img) {
		return getBorderWidth(img, 'top')
			+ getBorderWidth(img, 'bottom')
			+ img.get(0).naturalHeight;
	}

	// при наведении мышкой на области, расположенные поверх
	// скриншота, меняем src у картинки
	$('.case:not(.ok) .screenshot').each(function () {
		var screenshotContainer = $(this);
		var img = $('img:not(.cache)', screenshotContainer);
		var diffSrc = img.attr('src');

		$('.screenshot-toggle-area', screenshotContainer).each(function () {
			var target = $(this).attr('data-target');
			var targetImg = $('img.cache[data-target="' + target + '"]', screenshotContainer);
			var targetSrc = targetImg.attr('src');

			/**
			 * выставляем контейнеру высоту картинки с диффом, чтобы
			 * верстка не прыгала при смене src картинки, если они разные
			 */
			img.load(function () {
				screenshotContainer.css({
					height: getImageHeightWithBorder(img)
				});
				img.unbind('load');
			});

			$(this).hover(function () {
				img.attr('src', targetSrc);
			}, function () {
				img.attr('src', diffSrc);
			})
		});
	});
});
