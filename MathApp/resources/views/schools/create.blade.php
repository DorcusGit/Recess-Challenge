@extends('layouts.app')

@section('content')
<div class="container">
    <h1>Create School</h1>

    <form action="{{ route('schools.store') }}" method="POST">
        @csrf

        <div class="mb-3">
            <label for="schoolRegNo" class="form-label">School Registration Number</label>
            <input type="text" class="form-control" id="schoolRegNo" name="schoolRegNo" required>
        </div>

        <div class="mb-3">
            <label for="schoolName" class="form-label">School Name</label>
            <input type="text" class="form-control" id="schoolName" name="schoolName" required>
        </div>

        <div class="mb-3">
            <label for="district" class="form-label">District</label>
            <input type="text" class="form-control" id="district" name="district" required>
        </div>

        <div class="mb-3">
            <label for="schoolRepID" class="form-label">School Representative ID</label>
            <input type="text" class="form-control" id="schoolRepID" name="schoolRepID" required>
        </div>

        <div class="mb-3">
            <label for="emailAddress" class="form-label">Email Address</label>
            <input type="email" class="form-control" id="emailAddress" name="emailAddress" required>
        </div>

        <div class="mb-3">
            <label for="password" class="form-label">Password</label>
            <input type="password" class="form-control" id="password" name="password" required>
        </div>

        <button type="submit" class="btn btn-success">Submit</button>
    </form>
</div>
@endsection