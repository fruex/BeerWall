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

var summaries = new[]
{
    "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
};

app.MapGet("/weatherforecast", () =>
{
    var forecast =  Enumerable.Range(1, 5).Select(index =>
        new WeatherForecast
        (
            DateOnly.FromDateTime(DateTime.Now.AddDays(index)),
            Random.Shared.Next(-20, 55),
            summaries[Random.Shared.Next(summaries.Length)]
        ))
        .ToArray();
    return Results.Ok(new ApiEnvelope<WeatherForecast[]>(forecast));
})
.WithName("GetWeatherForecast");

var api = app.MapGroup("/api").RequireAuthorization();

// 1. SALDO (Balance)
api.MapGet("/balance", (ClaimsPrincipal user) => 
{
    var sampleBalances = new List<LocationBalance>
    {
        new("Pub Lewe", 125.50),
        new("Browariat", 89.00),
        new("Biała Małpa", 45.25),
        new("Czarna Małpa", 250.00),
    };
    return Results.Ok(new ApiEnvelope<List<LocationBalance>>(sampleBalances));
});

api.MapPost("/balance", (ClaimsPrincipal user, TopUpRequest req) => 
    Results.Ok(new ApiEnvelope<object>(new { Message = $"Doładowano kwotą {req.Amount}", NewBalance = 150.50 + req.Amount })));

// 2. KARTY (Cards)
api.MapGet("/cards", (ClaimsPrincipal user) => 
    Results.Ok(new ApiEnvelope<List<Card>>(new List<Card> { new(1, "4422", true), new(2, "9911", false) })));

api.MapGet("/cards/{id}", (int id) => 
    Results.Ok(new ApiEnvelope<CardDetails>(new CardDetails(id, "4422", "Active", "BeerWall Classic", 1250))));

// 3. HISTORIA I MIEJSCA
api.MapGet("/history", () => 
    Results.Ok(new ApiEnvelope<List<Transaction>>(new List<Transaction> { 
        new(1, "2023-10-20", -25.0, "Browar Stołeczny"), 
        new(2, "2023-10-19", 100.0, "Top-up") 
    })));

api.MapGet("/places", () => 
    Results.Ok(new ApiEnvelope<List<Place>>(new List<Place> { 
        new(1, "Browar Stołeczny", 45.0), 
        new(2, "Pub pod Kuflem", 12.50) 
    })));

// 4. OPERATORZY I AKTYWACJA
api.MapGet("/payment-operators", () => 
    Results.Ok(new ApiEnvelope<string[]>(new[] { "Blik", "Przelewy24", "Stripe" })));

api.MapPost("/card-activation", (CardActivationRequest req) => 
    Results.Ok(new ApiEnvelope<object>(new { CardId = req.CardId, IsActive = req.Activate, Status = "Success" })));

// 5. PROFIL (Punkty)
api.MapGet("/profile", (ClaimsPrincipal user) => {
    var sub = user.FindFirst(ClaimTypes.NameIdentifier)?.Value;
    return Results.Ok(new ApiEnvelope<object>(new { 
        GoogleId = sub, 
        Points = 1250, 
        Rank = "Beer Lover" 
    }));
});

app.Run();

record WeatherForecast(DateOnly Date, int TemperatureC, string? Summary)
{
    public int TemperatureF => 32 + (int)(TemperatureC / 0.5556);
}

// --- MODELE DTO ---
record TopUpRequest(double Amount, string Method);
record Card(int Id, string LastFour, bool IsDefault);
record CardDetails(int Id, string Number, string Status, string Type, int LoyaltyPoints);
record Transaction(int Id, string Date, double Amount, string Description);
record Place(int Id, string Name, double FundsAvailable);
record LocationBalance(string LocationName, double Balance);
record CardActivationRequest(int CardId, bool Activate);

record ApiEnvelope<T>(T Data);
