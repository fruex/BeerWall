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

api.MapGet("/cards", (ClaimsPrincipal user) =>
{
    var sampleCards = new List<CardResponse>
    {
        new("550e8400-e29b-41d4-a716-446655440000", "Karta Wirtualna", true, false),
        new("750e8460-e29b-41d4-a716-446655440001", "Karta fizyczna", true, true)
    };
    return Results.Ok(new ApiEnvelope<List<CardResponse>>(sampleCards));
});

api.MapGet("/history", () =>
{
    var sampleHistory = new List<TransactionResponse>
    {
        new("1", "Pilsner Urquell", "2024-11-24T19:30:00", "Pub Lewe", -12.50, 500),
        new("2", "Wino Chianti Classico", "2024-11-24T20:15:00", "Browariat", -28.00, 150),
        new("3", "Guinness Draught", "2024-11-24T21:00:00", "Biała Małpa", -15.00, 500),
        new("4", "Corona Extra", "2024-11-23T18:45:00", "Czarna Małpa", -11.00, 330),
        new("5", "Prosecco", "2024-11-23T19:30:00", "Pub Lewe", -22.00, 200),
        new("6", "Heineken", "2024-11-23T20:15:00", "Browariat", -10.50, 500),
        new("7", "Stella Artois", "2024-11-23T21:30:00", "Biała Małpa", -13.50, 500),
        new("8", "Tyskie Gronie", "2024-11-22T17:00:00", "Czarna Małpa", -9.50, 500),
        new("9", "Wino Malbec", "2024-11-22T18:30:00", "Pub Lewe", -32.00, 150),
        new("10", "IPA Craft Beer", "2024-11-22T19:45:00", "Browariat", -16.00, 500),
        new("11", "Żywiec Porter", "2024-11-21T20:00:00", "Biała Małpa", -14.00, 500),
        new("12", "Wino Sauvignon Blanc", "2024-11-21T20:45:00", "Czarna Małpa", -25.00, 150),
        new("13", "Peroni Nastro Azzurro", "2024-11-21T21:30:00", "Pub Lewe", -12.00, 330),
        new("14", "Desperados", "2024-11-21T22:15:00", "Browariat", -11.50, 400),
        new("15", "Wino Cabernet Sauvignon", "2024-11-20T19:00:00", "Biała Małpa", -30.00, 150),
        new("16", "Carlsberg", "2024-11-20T20:30:00", "Czarna Małpa", -10.00, 500),
        new("17", "Hoegaarden", "2024-11-20T21:15:00", "Pub Lewe", -13.00, 330)
    };
    return Results.Ok(new ApiEnvelope<List<TransactionResponse>>(sampleHistory));
});

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

app.Run();

// --- REQUEST MODELS ---
record TopUpRequest(int VenueId, int PaymentMethodId, double Amount);

// --- RESPONSE MODELS ---
record VenueBalanceResponse(int VenueId, string VenueName, double Balance, int LoyaltyPoints);
record CardResponse(string Id, string Name, bool IsActive, bool IsPhysical);
record TransactionResponse(string Id, string BeverageName, string Timestamp, string VenueName, double Amount, int VolumeMilliliters);
record PaymentMethodResponse(int Id, string Name, string Description, string Image, string Status);
record PaymentOperatorResponse(string Type, List<PaymentMethodResponse> PaymentMethods);

record ApiEnvelope<T>(T Data);
