using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Security.Claims;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options => {
        options.Authority = "https://accounts.google.com";
        options.TokenValidationParameters = new TokenValidationParameters {
            ValidateIssuer = true,
            ValidIssuer = "https://accounts.google.com",
            ValidateAudience = true,
            ValidAudience = "220522932694-ghamgoqpqtmb0vk9ajnouiqe2h52ateb.apps.googleusercontent.com",
            ValidateLifetime = true
        };
    });

builder.Services.AddAuthorization();

// Add services to the container.
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

var app = builder.Build();
app.UseAuthentication();
app.UseAuthorization();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();

var api = app.MapGroup("/api")/*.RequireAuthorization()*/;

api.MapGet("/balance", (ClaimsPrincipal user) =>
{
    var sampleVenueBalances = new List<VenueBalanceResponse>
    {
        new(1, "Pub Lewe", 125.50, 250),
        new(2, "Browariat", 89.00, 250),
        new(3, "Biała Małpa", 45.25, 250),
        new(4, "Czarna Małpa", 250.00, 250),
    };
    return Results.Ok(new ApiEnvelope<List<VenueBalanceResponse>>(sampleVenueBalances));
});

api.MapPost("/balance", (ClaimsPrincipal user, TopUpRequest req) =>
    Results.Ok(new ApiEnvelope<TopUpResponse>(new TopUpResponse($"Doładowano kwotą {req.Amount} metodą {req.PaymentMethodId}", 150.50 + req.Amount))));

api.MapGet("/history", () =>
{
    var sampleHistory = new List<TransactionResponse>
    {
        new("1", "Pilsner Urquell", new DateTime(2024, 11, 24, 19, 30, 0), -12.50, 500),
        new("2", "Wino Chianti Classico", new DateTime(2024, 11, 24, 20, 15, 0), -28.00, 150),
        new("3", "Guinness Draught", new DateTime(2024, 11, 24, 21, 0, 0), -15.00, 500),
        new("4", "Corona Extra", new DateTime(2024, 11, 23, 18, 45, 0), -11.00, 330),
        new("5", "Prosecco", new DateTime(2024, 11, 23, 19, 30, 0), -22.00, 200),
        new("6", "Heineken", new DateTime(2024, 11, 23, 20, 15, 0), -10.50, 500),
        new("7", "Stella Artois", new DateTime(2024, 11, 23, 21, 30, 0), -13.50, 500),
        new("8", "Tyskie Gronie", new DateTime(2024, 11, 22, 17, 0, 0), -9.50, 500),
        new("9", "Wino Malbec", new DateTime(2024, 11, 22, 18, 30, 0), -32.00, 150),
        new("10", "IPA Craft Beer", new DateTime(2024, 11, 22, 19, 45, 0), -16.00, 500),
        new("11", "Żywiec Porter", new DateTime(2024, 11, 21, 20, 0, 0), -14.00, 500),
        new("12", "Wino Sauvignon Blanc", new DateTime(2024, 11, 21, 20, 45, 0), -25.00, 150),
        new("13", "Peroni Nastro Azzurro", new DateTime(2024, 11, 21, 21, 30, 0), -12.00, 330),
        new("14", "Desperados", new DateTime(2024, 11, 21, 22, 15, 0), -11.50, 400),
        new("15", "Wino Cabernet Sauvignon", new DateTime(2024, 11, 20, 19, 0, 0), -30.00, 150),
        new("16", "Carlsberg", new DateTime(2024, 11, 20, 20, 30, 0), -10.00, 500),
        new("17", "Hoegaarden", new DateTime(2024, 11, 20, 21, 15, 0), -13.00, 330)
    };
    return Results.Ok(new ApiEnvelope<List<TransactionResponse>>(sampleHistory));
});

api.MapGet("/places", () =>
    Results.Ok(new ApiEnvelope<List<PlaceResponse>>(new List<PlaceResponse>
    {
        new(1, "Browar Stołeczny", 45.0),
        new(2, "Pub pod Kuflem", 12.50)
    })));

api.MapGet("/payment-operators", () =>
{
    var paymentOperators = new List<PaymentOperatorResponse>
    {
        new("BLIK",
        [
            new(2007, "BLIK", "Płatność BLIKIEM", "https://static.sandbox.paynow.pl/payment-method-icons/2007.png",
                "ENABLED")
        ])
    };
    return Results.Ok(new ApiEnvelope<List<PaymentOperatorResponse>>(paymentOperators));
});

api.MapGet("/profile", (ClaimsPrincipal user) => {
    return Results.Ok(new ApiEnvelope<ProfileResponse>(new ProfileResponse(250)));
});

app.Run();

// --- REQUEST MODELS ---
record TopUpRequest(int VenueId, int PaymentMethodId, double Amount);

// --- RESPONSE MODELS ---
record VenueBalanceResponse(int VenueId, string VenueName, double Balance, int LoyaltyPoints);
record TransactionResponse(string Id, string BeverageName, DateTime Timestamp, double Amount, int VolumeMilliliters);
record PlaceResponse(int Id, string VenueName, double FundsAvailable);
record ProfileResponse(int LoyaltyPoints);
record PaymentMethodResponse(int Id, string Name, string Description, string Image, string Status);
record PaymentOperatorResponse(string Type, List<PaymentMethodResponse> PaymentMethods);
record TopUpResponse(string Message, double NewBalance);

record ApiEnvelope<T>(T Data);
