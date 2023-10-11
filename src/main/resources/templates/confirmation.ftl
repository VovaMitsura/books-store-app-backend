<#setting number_format="computer">

<head>
    <meta charset="UTF-8" />
    <title>LandscapeOfFreedomEmailConfirmation</title>
    <style>
        .text {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            text-align: left;
            color: rgba(245, 200, 122, 0.99);
        }

        .email {
            background-color: #474950;
            padding: 40px 60px 40px 60px;
            border-radius: 32px;

            box-shadow: 0 0 32px 6px rgba(0, 0, 0, 0.34);
        }
    </style>
</head>

<body>
<div class="text">
    <div class="email">
        <h1>Hello</h1>
        <p>
            Dear ${user.firstName} ${user.lastName}, <br><br />
            Thanks for registering in Books Store. <br>
        </p>


        <h3>Confirmation letter</h3>


        <p> Press <a href=`${confirmation}`>link</a> to enable your account.
    </div>
</div>

</body>
