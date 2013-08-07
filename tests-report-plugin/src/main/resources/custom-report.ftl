<script type="text/javascript" src="http://yandex.st/jquery/2.0.3/jquery.min.js"></script>
<script type="text/javascript" src="http://yandex.st/jquery-ui/1.10.0/jquery-ui.min.js"></script>
<script type="text/javascript" src="http://yandex.st/bootstrap/2.3.1/js/bootstrap.min.js"></script>
<link type="text/css" href="http://yandex.st/bootstrap/2.3.1/css/bootstrap.min.css" rel="stylesheet"/>

<link type="text/css" href="custom/custom.css" rel="stylesheet"/>
<script type="text/javascript" src="custom/custom.js"></script>

<section id="custom-report">
    <div class="page-header">
        <h2>Screenshot Report</h2>
    </div>
<#list testCases as case>
    <div class="case expanded ${case.status?lower_case}">
        <h3 class="title">
            <i class="icon-chevron-right"></i>
            <i class="icon-chevron-down"></i>
            <span>${case.title}</span>
            <span class="status">${case.status}</span>
        </h3>
        <div class="urls">
            <a href="${case.origin.pageUrl}" target="_blank">origin</a>
            <a href="${case.modified.pageUrl}" target="_blank">modified</a>
        </div>
        <div class="screenshot">
            <img src="custom/${case.diff.fileName}"/>
			<#if case.status != 'OK'>
                <img class="cache" data-target="origin" src="custom/${case.origin.fileName}"/>
                <img class="cache" data-target="modified" src="custom/${case.modified.fileName}"/>
                <div class="screenshot-toggle">
                    <div>
                        <div class="screenshot-toggle-area" data-target="origin">
                            <div>origin</div>
                            <div>(hover to show)</div>
                        </div>
                        <div class="screenshot-toggle-area" data-target="modified">
                            <div>modified</div>
                            <div>(hover to show)</div>
                        </div>
                    </div>
                </div>
			</#if>
        </div>
    </div>
</#list>
</section>
